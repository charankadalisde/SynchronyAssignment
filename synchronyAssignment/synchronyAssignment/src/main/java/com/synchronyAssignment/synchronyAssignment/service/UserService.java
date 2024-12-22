package com.synchronyAssignment.synchronyAssignment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronyAssignment.synchronyAssignment.configurations.ExecutorConfiguration;
import com.synchronyAssignment.synchronyAssignment.entity.User;
import com.synchronyAssignment.synchronyAssignment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ExecutorService executorService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public User createUser(User user) throws Exception {
        User newUser = userRepository.save(user);
        // Cache the newly created user in Redis
        String cachedUser = objectMapper.writeValueAsString(newUser);
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
//        valueOps.set("user:" + newUser.getId(), cachedUser, 100, TimeUnit.SECONDS);
        valueOps.set("user:" + newUser.getId(), cachedUser);
        return newUser;

    }

    public ResponseEntity<User> getUser(long id) {
        /*if the user is present in cache then return
        user from cache otherwise take a db call */
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String cachedUser = valueOperations.get("user:" + id);
        if (cachedUser != null) {
            try {
                // Deserialize the cached JSON string to a User object
                User user = objectMapper.readValue(cachedUser, User.class);
                return ResponseEntity.ok(user);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).build(); // Internal Server Error
            }
        } else {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                try {
                    // Serialize the User object to a JSON string and cache it
                    String userJson = objectMapper.writeValueAsString(user);
                    valueOperations.set("user:" + id, userJson);
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(500).build(); // Internal Server Error
                }
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }
        }
    }

    public ResponseEntity<List<User>> getBulkUsers(List<Long> ids) throws ExecutionException, InterruptedException {

        List<Future<User>> futureList = getUsersConcurrentry(ids);
        List<User> userList = new ArrayList<>();
        for (Future<User> future : futureList) {
            if (future.get() != null) {
                userList.add(future.get());
            }
        }
        return ResponseEntity.ok(userList);
    }

    private List<Future<User>> getUsersConcurrentry(List<Long> ids) {
        List<Future<User>> futureList = new ArrayList<>();
        for (Long id : ids) {
            futureList.add(executorService.submit(() -> Optional.of(getUser(id)).orElse(null).getBody()));
        }
        return futureList;
    }

    public ResponseEntity<String> updateUser(Long id, User updatedUser) throws JsonProcessingException {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User existingUser = userOpt.get();
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());
            userRepository.save(existingUser);
            ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
            String cachedUser = objectMapper.writeValueAsString(existingUser);
            valueOps.set("user:" + existingUser.getId(), cachedUser);

            return ResponseEntity.ok("User updated in mysql Database and cached in Redis");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<String> deleteUserById(Long id) {
        if(userRepository.existsById(id))
        {
            userRepository.deleteById(id);
            redisTemplate.delete("user:"+id);
            return ResponseEntity.ok("user is deleted from db and cache");
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}
