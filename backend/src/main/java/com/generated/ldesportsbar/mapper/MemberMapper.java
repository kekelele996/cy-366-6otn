package com.generated.ldesportsbar.mapper;

import java.math.BigDecimal;
import com.generated.ldesportsbar.model.Member;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MemberMapper {

  @Select("SELECT * FROM members WHERE phone = #{phone}")
  Member findByPhone(@Param("phone") String phone);

  @Select("SELECT * FROM members WHERE id = #{id}")
  Member findById(@Param("id") Long id);

  /** 悲观锁锁定会员行，保证余额与时长包的扣减串行化。 */
  @Select("SELECT * FROM members WHERE id = #{id} FOR UPDATE")
  Member lockById(@Param("id") Long id);

  @Insert("INSERT INTO members (phone, name, balance) VALUES (#{phone}, #{name}, #{balance})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(Member member);

  @Update("UPDATE members SET balance = #{balance} WHERE id = #{id}")
  int updateBalance(@Param("id") Long id, @Param("balance") BigDecimal balance);

  @Update("UPDATE members SET total_usage_minutes = #{totalUsageMinutes} WHERE id = #{id}")
  int updateTotalUsage(@Param("id") Long id, @Param("totalUsageMinutes") Integer totalUsageMinutes);
}
