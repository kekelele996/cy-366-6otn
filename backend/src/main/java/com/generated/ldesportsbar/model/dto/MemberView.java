package com.generated.ldesportsbar.model.dto;

import com.generated.ldesportsbar.model.entity.Member;
import com.generated.ldesportsbar.model.entity.TimePackage;
import java.util.List;

public record MemberView(Member member, List<TimePackage> packages, int totalRemainingMinutes) {
}
