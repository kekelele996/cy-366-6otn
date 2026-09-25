export interface FeatureItem {
  id: number;
  title: string;
  description: string;
  status: string;
  metric: string;
}

export interface KpiItem {
  label: string;
  value: string;
  trend: string;
  tone: string;
}

export interface OperationRecord {
  key: string;
  name: string;
  owner: string;
  status: string;
  metric: string;
  priority: string;
}

export interface OverviewResponse {
  appName: string;
  appCode: string;
  description: string;
  features: FeatureItem[];
  kpis: KpiItem[];
  records: OperationRecord[];
}

export interface Member {
  id: number;
  phone: string;
  name: string;
  balance: number;
  createdAt: string;
}

export interface TimePackage {
  id: number;
  memberId: number;
  totalMinutes: number;
  remainingMinutes: number;
  price: number;
  status: "ACTIVE" | "DEPLETED" | "EXPIRED";
  purchasedAt: string;
  expiresAt: string;
}

export interface MemberView {
  member: Member;
  packages: TimePackage[];
  totalRemainingMinutes: number;
}

export type MachineStatus = "IDLE" | "IN_USE" | "FAULT";

export interface Machine {
  id: number;
  code: string;
  zone: string;
  status: MachineStatus;
}

export interface AccountFlow {
  id: number;
  memberId: number;
  sessionId: number | null;
  type: string;
  amount: number;
  minutes: number;
  balanceAfter: number;
  remark: string;
  createdAt: string;
}

export interface MachineSession {
  id: number;
  requestId: string;
  memberId: number;
  machineId: number;
  plannedMinutes: number;
  prepaidPackageMinutes: number;
  prepaidAmount: number;
  actualMinutes: number | null;
  actualPackageMinutes: number | null;
  actualAmount: number | null;
  refundAmount: number | null;
  status: "ACTIVE" | "CLOSED";
  startedAt: string;
  endedAt: string | null;
}

export interface SessionResult {
  session: MachineSession;
  machineCode: string;
  replayed: boolean;
}

export interface CostEstimate {
  packageMinutes: number;
  balance: number;
}
