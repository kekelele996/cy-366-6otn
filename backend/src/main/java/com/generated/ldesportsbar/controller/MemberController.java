package com.generated.ldesportsbar.controller;

import com.generated.ldesportsbar.model.dto.CreateMemberRequest;
import com.generated.ldesportsbar.model.dto.MemberView;
import com.generated.ldesportsbar.model.dto.PurchasePackageRequest;
import com.generated.ldesportsbar.model.dto.RechargeRequest;
import com.generated.ldesportsbar.model.entity.AccountFlow;
import com.generated.ldesportsbar.model.entity.Member;
import com.generated.ldesportsbar.service.MemberService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {
  private final MemberService memberService;

  public MemberController(MemberService memberService) {
    this.memberService = memberService;
  }

  @PostMapping({"/members", "/api/members"})
  public MemberView create(@Valid @RequestBody CreateMemberRequest request) {
    return memberService.createOrFind(request.phone(), request.name());
  }

  @GetMapping({"/members", "/api/members"})
  public List<Member> list() {
    return memberService.listMembers();
  }

  @GetMapping({"/members/{id}", "/api/members/{id}"})
  public MemberView detail(@PathVariable Long id) {
    return memberService.getView(id);
  }

  @PostMapping({"/members/{id}/recharge", "/api/members/{id}/recharge"})
  public MemberView recharge(@PathVariable Long id, @Valid @RequestBody RechargeRequest request) {
    return memberService.recharge(id, request.amount(), request.requestId());
  }

  @PostMapping({"/members/{id}/packages", "/api/members/{id}/packages"})
  public MemberView purchasePackage(@PathVariable Long id,
      @RequestBody(required = false) PurchasePackageRequest request) {
    return memberService.purchasePackage(id, request == null ? null : request.requestId());
  }

  @GetMapping({"/members/{id}/flows", "/api/members/{id}/flows"})
  public List<AccountFlow> flows(@PathVariable Long id) {
    return memberService.listFlows(id);
  }
}
