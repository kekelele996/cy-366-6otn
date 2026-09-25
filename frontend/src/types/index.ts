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
  name: string | null;
  balance: number;
  totalUsageMinutes: number;
  createdAt: string;
}

export interface PackageView {
  id: number;
  packageMinutes: number;
  remainingMinutes: number;
  expiresAt: string;
  active: boolean;
}

export interface TransactionView {
  id: number;
  sessionId: number | null;
  type: string;
  amount: number;
  minutes: number;
  balanceAfter: number;
  description: string;
  createdAt: string;
}

export interface MemberProfile {
  id: number;
  phone: string;
  name: string | null;
  balance: number;
  remainingMinutes: number;
  totalUsageMinutes: number;
  createdAt: string;
  packages: PackageView[];
  transactions: TransactionView[];
}

export interface SessionBrief {
  sessionId: number;
  memberId: number;
  phone: string;
  memberName: string | null;
  plannedMinutes: number;
  startTime: string;
}

export interface SeatView {
  id: number;
  seatNo: string;
  zone: string;
  status: "IDLE" | "IN_USE" | "BROKEN";
  activeSession: SessionBrief | null;
}

export interface StartSessionPayload {
  phone: string;
  seatId: number;
  plannedMinutes: number;
  idempotencyKey: string;
}

export interface StartSessionResult {
  sessionId: number;
  seatId: number;
  seatNo: string;
  startTime: string;
  plannedMinutes: number;
  packageMinutesUsed: number;
  balanceCharged: number;
  balanceAfter: number;
  idempotentReplay: boolean;
}

export interface StopSessionResult {
  sessionId: number;
  seatId: number;
  seatNo: string;
  startTime: string;
  endTime: string;
  plannedMinutes: number;
  actualMinutes: number;
  packageMinutesUsed: number;
  packageMinutesRefunded: number;
  balanceCharged: number;
  balanceRefunded: number;
  balanceAfter: number;
}
