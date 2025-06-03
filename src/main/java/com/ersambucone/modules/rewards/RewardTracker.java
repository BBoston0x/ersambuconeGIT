package com.ersambucone.modules.rewards;

import java.util.ArrayList;
import java.util.List;

public class RewardTracker {
    private static final List<String> claimedRewards = new ArrayList<>();

    public static void addReward(String rewardId) {
        claimedRewards.add(rewardId);
    }
}