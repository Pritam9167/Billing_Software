package com.main.springboot.services;

import com.main.springboot.model.StockConfig;
import com.main.springboot.model.User;
import com.main.springboot.repository.StockConfigRepository;
import com.main.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class StockConfigService {

    @Autowired
    private StockConfigRepository configRepo;

    @Autowired
    private UserRepository userRepository;

    // ✅ Utility to get logged-in user
    private User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();
        return userRepository.findByUsername(username);
    }

    public int getThreshold() {
        return configRepo.findByCreatedBy(getLoggedInUser())
                .map(StockConfig::getLowStockThreshold)
                .orElse(5); // default threshold
    }

    public void updateThreshold(int newValue) {
        User user = getLoggedInUser();
        StockConfig config = configRepo.findByCreatedBy(user)
                .orElse(new StockConfig());

        config.setLowStockThreshold(newValue);
        config.setCreatedBy(user); // attach user
        configRepo.save(config);
    }
}
