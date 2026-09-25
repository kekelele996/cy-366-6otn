package com.generated.ldesportsbar.mapper;

import com.generated.ldesportsbar.model.entity.Member;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MemberMapper {
  @Insert("INSERT INTO members (phone, name, balance) VALUES (#{phone}, #{name}, #{balance})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Member member);

  @Select("SELECT * FROM members WHERE id = #{id}")
  Member findById(Long id);

  @Select("SELECT * FROM members WHERE id = #{id} FOR UPDATE")
  Member findByIdForUpdate(Long id);

  @Select("SELECT * FROM members WHERE phone = #{phone}")
  Member findByPhone(String phone);

  @Select("SELECT * FROM members ORDER BY id DESC")
  List<Member> findAll();

  @Update("UPDATE members SET balance = #{balance} WHERE id = #{id}")
  int updateBalance(Member member);
}
