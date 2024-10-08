package com.example.stock.facade;

import com.example.stock.service.StockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockStockFacade {
    private RedissonClient redissonClient;
    private StockService stockService;

    public RedissonLockStockFacade(RedissonClient redissonClient, StockService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }
    public void decrease(Long key, Long quantity){
        RLock rLock = redissonClient.getLock(String.valueOf(key));
        try {
            boolean available = rLock.tryLock(5, 1, TimeUnit.SECONDS);

            if(!available){
                System.out.println("lock 획득실패");
                return;
            }
            stockService.decrease(key,quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            rLock.unlock();
        }
    }
}
