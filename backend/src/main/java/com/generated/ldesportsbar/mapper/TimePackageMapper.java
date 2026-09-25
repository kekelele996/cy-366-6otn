package com.generated.ldesportsbar.mapper;

import com.generated.ldesportsbar.model.entity.TimePackage;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TimePackageMapper {
  @Insert("INSERT INTO time_packages (member_id, total_minutes, remaining_minutes, price, status, purchased_at, expires_at) "
      + "VALUES (#{memberId}, #{totalMinutes}, #{remainingMinutes}, #{price}, #{status}, #{purchasedAt}, #{expiresAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(TimePackage timePackage);

  @Select("SELECT * FROM time_packages WHERE id = #{id}")
  TimePackage findById(Long id);

  /** 有效时长包：未用完且未过期，按到期时间升序（最早到期先扣） */
  @Select("SELECT * FROM time_packages WHERE member_id = #{memberId} AND status = 'ACTIVE' "
      + "AND remaining_minutes > 0 AND expires_at > #{now} ORDER BY expires_at ASC, id ASC")
  List<TimePackage> findValidByMember(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);

  @Select("SELECT * FROM time_packages WHERE member_id = #{memberId} ORDER BY id DESC")
  List<TimePackage> findByMember(Long memberId);

  @Update("UPDATE time_packages SET remaining_minutes = #{remainingMinutes}, status = #{status} WHERE id = #{id}")
  int updateRemainingAndStatus(TimePackage timePackage);

  @Update("UPDATE time_packages SET status = 'EXPIRED' WHERE status = 'ACTIVE' AND expires_at <= #{now}")
  int expireStale(LocalDateTime now);
}
