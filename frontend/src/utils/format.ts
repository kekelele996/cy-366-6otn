export function formatMoney(value: number): string {
  return `¥${value.toFixed(2)}`;
}

export function formatSignedMoney(value: number): string {
  if (value === 0) {
    return "—";
  }
  const sign = value > 0 ? "+" : "-";
  return `${sign}¥${Math.abs(value).toFixed(2)}`;
}

export function formatSignedMinutes(minutes: number): string {
  if (minutes === 0) {
    return "—";
  }
  const sign = minutes > 0 ? "+" : "-";
  return `${sign}${Math.abs(minutes)} 分钟`;
}

export function formatMinutes(minutes: number): string {
  if (minutes < 60) {
    return `${minutes} 分钟`;
  }
  const hours = Math.floor(minutes / 60);
  const rest = minutes % 60;
  return rest === 0 ? `${hours} 小时` : `${hours} 小时 ${rest} 分`;
}

export function formatDateTime(value: string | null): string {
  if (!value) {
    return "—";
  }
  return value.replace("T", " ").slice(0, 19);
}
