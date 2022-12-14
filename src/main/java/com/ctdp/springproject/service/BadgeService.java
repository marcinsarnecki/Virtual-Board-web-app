package com.ctdp.springproject.service;

import com.ctdp.springproject.repository.BadgeRepository;
import com.ctdp.springproject.model.Badge;
import com.ctdp.springproject.model.Color;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BadgeService {
    private final BadgeRepository badgeRepository;

    public BadgeService(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }
    @Transactional
    public Badge add(Color color) {
        Badge badge = new Badge(color);
        badgeRepository.save(badge);
        return badge;
    }

    @Transactional
    public void deleteById(Long id) {
        badgeRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll() { badgeRepository.deleteCustom(); }

}
