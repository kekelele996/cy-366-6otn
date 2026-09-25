package com.generated.ldesportsbar.mapper;

import com.generated.ldesportsbar.model.entity.MachineSession;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MachineSessionMapper {
  @Insert("INSERT INTO machine_sessions (request_id, member_id, machine_id, planned_minutes, "
      + "prepaid_package_minutes, prepaid_amount, status, started_at) "
      + "VALUES (#{requestId}, #{memberId}, #{machineId}, #{plannedMinutes}, "
      + "#{prepaidPackageMinutes}, #{prepaidAmount}, #{status}, #{startedAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(MachineSession session);

  @Select("SELECT * FROM machine_sessions WHERE request_id = #{requestId}")
  MachineSession findByRequestId(String requestId);

  @Select("SELECT * FROM machine_sessions WHERE id = #{id}")
  MachineSession findById(Long id);

  @Select("SELECT * FROM machine_sessions WHERE id = #{id} FOR UPDATE")
  MachineSession findByIdForUpdate(Long id);

  @Select("SELECT * FROM machine_sessions WHERE status = 'ACTIVE' ORDER BY id DESC")
  List<MachineSession> findActive();

  @Update("UPDATE machine_sessions SET status = 'CLOSED', actual_minutes = #{actualMinutes}, "
      + "actual_package_minutes = #{actualPackageMinutes}, actual_amount = #{actualAmount}, "
      + "refund_amount = #{refundAmount}, ended_at = #{endedAt} WHERE id = #{id}")
  int close(MachineSession session);
}
