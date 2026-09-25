package com.generated.ldesportsbar.mapper;

import com.generated.ldesportsbar.model.entity.AccountFlow;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountFlowMapper {
  @Insert("INSERT INTO account_flows (request_id, member_id, session_id, type, amount, minutes, balance_after, remark, created_at) "
      + "VALUES (#{requestId}, #{memberId}, #{sessionId}, #{type}, #{amount}, #{minutes}, #{balanceAfter}, #{remark}, #{createdAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(AccountFlow flow);

  @Select("SELECT * FROM account_flows WHERE member_id = #{memberId} ORDER BY id DESC LIMIT 100")
  List<AccountFlow> findByMember(Long memberId);

  @Select("SELECT * FROM account_flows WHERE request_id = #{requestId}")
  AccountFlow findByRequestId(String requestId);
}
