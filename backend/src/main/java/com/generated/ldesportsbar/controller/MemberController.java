package com.generated.ldesportsbar.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.generated.ldesportsbar.dto.CreateMemberRequest;
import com.generated.ldesportsbar.dto.RechargeRequest;
import com.generated.ldesportsbar.model.Member;
import com.generated.ldesportsbar.model.MemberProfile;
import com.generated.ldesportsbar.service.MemberService;

@RestController
@RequestMapping({"/api/members", "/members"})
public class MemberController {

  private final MemberService memberService;

  public MemberController(MemberService memberService) {
    this.memberService = memberService;
  }

  /** 按手机号建会员。 */
  @PostMapping
  public Member createMember(@Valid @RequestBody CreateMemberRequest request) {
    return memberService.createMember(request.getPhone(), request.getName());
  }

  /** 按手机号查账户：余额、剩余时长、时长包与流水。 */
  @GetMapping
  public MemberProfile getMember(@RequestParam String phone) {
    return memberService.getProfile(phone);
  }

  /** 余额充值。 */
  @PostMapping("/{phone}/recharge")
  public MemberProfile recharge(@PathVariable String phone,
                                @Valid @RequestBody RechargeRequest request) {
    return memberService.recharge(phone, request.getAmount());
  }

  /** 购买 10 小时时长包。 */
  @PostMapping("/{phone}/packages")
  public MemberProfile buyPackage(@PathVariable String phone) {
    return memberService.buyTenHourPackage(phone);
  }
}
