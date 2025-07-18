package com.main.springboot.services;

import com.main.springboot.model.StockConfig;
import com.main.springboot.repository.StockConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockConfigService {

    @Autowired
    private StockConfigRepository configRepo;

    public int getThreshold() {
        return configRepo.findAll().stream().findFirst().map(StockConfig::getLowStockThreshold).orElse(5);
    }

    public void updateThreshold(int newValue) {
        StockConfig config = configRepo.findAll().stream().findFirst().orElse(new StockConfig());
        config.setLowStockThreshold(newValue);
        configRepo.save(config);
    }
}

