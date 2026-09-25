package com.generated.ldesportsbar.mapper;

import java.util.List;
import com.generated.ldesportsbar.model.Transaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TransactionMapper {

  @Insert("INSERT INTO transactions (member_id, session_id, package_id, type, amount, minutes, "
    + "balance_after, description) VALUES (#{memberId}, #{sessionId}, #{packageId}, #{type}, "
    + "#{amount}, #{minutes}, #{balanceAfter}, #{description})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(Transaction transaction);

  @Select("SELECT * FROM transactions WHERE member_id = #{memberId} "
    + "ORDER BY created_at DESC, id DESC")
  List<Transaction> findByMemberId(@Param("memberId") Long memberId);

  /** 取一次开机在开机时产生的扣减流水，停机时按这些流水原路退回。 */
  @Select("SELECT * FROM transactions WHERE session_id = #{sessionId} "
    + "AND type IN ('SESSION_PACKAGE_DEDUCT', 'SESSION_BALANCE_DEDUCT') "
    + "ORDER BY id ASC")
  List<Transaction> findStartDeductions(@Param("sessionId") Long sessionId);
}
