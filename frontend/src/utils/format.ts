const TRANSACTION_LABELS: Record<string, string> = {
  RECHARGE: "余额充值",
  PACKAGE_PURCHASE: "购买时长包",
  SESSION_PACKAGE_DEDUCT: "开机扣时长",
  SESSION_BALANCE_DEDUCT: "开机扣余额",
  SESSION_PACKAGE_REFUND: "停机退时长",
  SESSION_BALANCE_REFUND: "停机退余额",
};

export function transactionLabel(type: string): string {
  return TRANSACTION_LABELS[type] ?? type;
}

export function formatMoney(value: number | string | null | undefined): string {
  const num = typeof value === "string" ? Number(value) : value ?? 0;
  return `¥${Number(num).toFixed(2)}`;
}

export function formatMinutes(minutes: number | null | undefined): string {
  const value = minutes ?? 0;
  if (value === 0) {
    return "0 分钟";
  }
  const hours = Math.floor(value / 60);
  const rest = value % 60;
  if (hours === 0) {
    return `${rest} 分钟`;
  }
  if (rest === 0) {
    return `${hours} 小时`;
  }
  return `${hours} 小时 ${rest} 分`;
}

export function formatDateTime(value: string | null | undefined): string {
  if (!value) {
    return "-";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} `
    + `${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

/** 生成开机请求幂等键：同一次提交保持不变，重复提交不会多扣。 */
export function createIdempotencyKey(): string {
  if (typeof crypto !== "undefined" && typeof crypto.randomUUID === "function") {
    return crypto.randomUUID().replace(/-/g, "");
  }
  return `s${Date.now()}${Math.random().toString(36).slice(2, 12)}`;
}
