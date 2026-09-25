package com.generated.ldesportsbar.mapper;

import com.generated.ldesportsbar.model.entity.Machine;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MachineMapper {
  @Select("SELECT * FROM machines ORDER BY code ASC")
  List<Machine> findAll();

  @Select("SELECT * FROM machines WHERE id = #{id}")
  Machine findById(Long id);

  /** 只有空闲机位才能被锁定，返回 0 表示机位已被占用或故障 */
  @Update("UPDATE machines SET status = 'IN_USE' WHERE id = #{id} AND status = 'IDLE'")
  int occupy(Long id);

  @Update("UPDATE machines SET status = 'IDLE' WHERE id = #{id} AND status = 'IN_USE'")
  int release(Long id);

  @Select("SELECT COUNT(*) FROM machines")
  int count();

  @Insert("INSERT INTO machines (code, zone, status) VALUES (#{code}, #{zone}, #{status})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Machine machine);
}
