package com.generated.ldesportsbar.mapper;

import com.generated.ldesportsbar.model.entity.SessionDeduction;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SessionDeductionMapper {
  @Insert("INSERT INTO session_deductions (session_id, source_type, package_id, minutes, amount) "
      + "VALUES (#{sessionId}, #{sourceType}, #{packageId}, #{minutes}, #{amount})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(SessionDeduction deduction);

  @Select("SELECT * FROM session_deductions WHERE session_id = #{sessionId} ORDER BY id ASC")
  List<SessionDeduction> findBySession(Long sessionId);

  @Update("UPDATE session_deductions SET refunded_minutes = #{refundedMinutes}, refunded_amount = #{refundedAmount} WHERE id = #{id}")
  int updateRefund(SessionDeduction deduction);
}
