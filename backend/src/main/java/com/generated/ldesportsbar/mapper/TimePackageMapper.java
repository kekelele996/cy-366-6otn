package com.generated.ldesportsbar.mapper;

import java.time.LocalDateTime;
import java.util.List;
import com.generated.ldesportsbar.model.TimePackage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TimePackageMapper {

  /** 会员名下全部时长包，按到期时间升序（最早到期在前）。 */
  @Select("SELECT * FROM time_packages WHERE member_id = #{memberId} ORDER BY expires_at ASC, id ASC")
  List<TimePackage> findByMemberId(@Param("memberId") Long memberId);

  /** 锁定未到期且仍有剩余的时长包，消费时按最早到期优先扣减（在服务层排序）。 */
  @Select("SELECT * FROM time_packages "
    + "WHERE member_id = #{memberId} AND remaining_minutes > 0 AND expires_at > #{now} "
    + "FOR UPDATE")
  List<TimePackage> lockUsable(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);

  @Select("SELECT * FROM time_packages WHERE id = #{id} FOR UPDATE")
  TimePackage lockById(@Param("id") Long id);

  @Select("SELECT COALESCE(SUM(remaining_minutes), 0) FROM time_packages "
    + "WHERE member_id = #{memberId} AND remaining_minutes > 0 AND expires_at > #{now}")
  Integer sumUsableMinutes(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);

  @Insert("INSERT INTO time_packages (member_id, package_minutes, remaining_minutes, expires_at) "
    + "VALUES (#{memberId}, #{packageMinutes}, #{remainingMinutes}, #{expiresAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(TimePackage timePackage);

  @Update("UPDATE time_packages SET remaining_minutes = #{remainingMinutes} WHERE id = #{id}")
  int updateRemaining(@Param("id") Long id, @Param("remainingMinutes") Integer remainingMinutes);
}
