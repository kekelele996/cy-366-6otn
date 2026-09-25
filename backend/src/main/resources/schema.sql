-- 电竞馆上机管理系统数据库结构（H2 MySQL 模式 / MySQL 8.0 通用，幂等可重复执行）

CREATE TABLE IF NOT EXISTS members (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  phone VARCHAR(20) NOT NULL,
  name VARCHAR(60) NOT NULL DEFAULT '',
  balance DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_members_phone UNIQUE (phone)
);

CREATE TABLE IF NOT EXISTS time_packages (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  member_id BIGINT NOT NULL,
  total_minutes INT NOT NULL,
  remaining_minutes INT NOT NULL,
  price DECIMAL(12,2) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  purchased_at TIMESTAMP NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  CONSTRAINT fk_time_packages_member FOREIGN KEY (member_id) REFERENCES members (id)
);

CREATE TABLE IF NOT EXISTS machines (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(20) NOT NULL,
  zone VARCHAR(40) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'IDLE',
  CONSTRAINT uk_machines_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS machine_sessions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  request_id VARCHAR(64) NOT NULL,
  member_id BIGINT NOT NULL,
  machine_id BIGINT NOT NULL,
  planned_minutes INT NOT NULL,
  prepaid_package_minutes INT NOT NULL DEFAULT 0,
  prepaid_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  actual_minutes INT NULL,
  actual_package_minutes INT NULL,
  actual_amount DECIMAL(12,2) NULL,
  refund_amount DECIMAL(12,2) NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  started_at TIMESTAMP NOT NULL,
  ended_at TIMESTAMP NULL,
  CONSTRAINT uk_machine_sessions_request UNIQUE (request_id),
  CONSTRAINT fk_machine_sessions_member FOREIGN KEY (member_id) REFERENCES members (id),
  CONSTRAINT fk_machine_sessions_machine FOREIGN KEY (machine_id) REFERENCES machines (id)
);

CREATE TABLE IF NOT EXISTS session_deductions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_id BIGINT NOT NULL,
  source_type VARCHAR(20) NOT NULL,
  package_id BIGINT NULL,
  minutes INT NOT NULL DEFAULT 0,
  amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  refunded_minutes INT NOT NULL DEFAULT 0,
  refunded_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  CONSTRAINT fk_session_deductions_session FOREIGN KEY (session_id) REFERENCES machine_sessions (id)
);

CREATE TABLE IF NOT EXISTS account_flows (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  request_id VARCHAR(64) NULL,
  member_id BIGINT NOT NULL,
  session_id BIGINT NULL,
  type VARCHAR(30) NOT NULL,
  amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  minutes INT NOT NULL DEFAULT 0,
  balance_after DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  remark VARCHAR(200) NOT NULL DEFAULT '',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_account_flows_request UNIQUE (request_id),
  CONSTRAINT fk_account_flows_member FOREIGN KEY (member_id) REFERENCES members (id)
);

CREATE TABLE IF NOT EXISTS operation_records (
  id INT AUTO_INCREMENT PRIMARY KEY,
  module_name VARCHAR(120) NOT NULL,
  owner_name VARCHAR(80) NOT NULL,
  status VARCHAR(40) NOT NULL,
  metric VARCHAR(40) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
