import { API_BASE_URL } from "../constants/app";
import type {
  AccountFlow,
  Machine,
  MachineSession,
  Member,
  MemberView,
  OverviewResponse,
  SessionResult,
} from "../types";

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: { Accept: "application/json", "Content-Type": "application/json" },
    ...options,
  });

  if (!response.ok) {
    let message = `请求失败（${response.status}）`;
    try {
      const body = (await response.json()) as { message?: string };
      if (body.message) {
        message = body.message;
      }
    } catch {
      // 保留默认错误信息
    }
    throw new Error(message);
  }

  return response.json() as Promise<T>;
}

export function fetchOverview(): Promise<OverviewResponse> {
  return request<OverviewResponse>("/overview");
}

export function findOrCreateMember(phone: string, name: string): Promise<MemberView> {
  return request<MemberView>("/members", {
    method: "POST",
    body: JSON.stringify({ phone, name }),
  });
}

export function fetchMembers(): Promise<Member[]> {
  return request<Member[]>("/members");
}

export function fetchMemberView(memberId: number): Promise<MemberView> {
  return request<MemberView>(`/members/${memberId}`);
}

export function rechargeMember(memberId: number, amount: number, requestId: string): Promise<MemberView> {
  return request<MemberView>(`/members/${memberId}/recharge`, {
    method: "POST",
    body: JSON.stringify({ amount, requestId }),
  });
}

export function purchasePackage(memberId: number, requestId: string): Promise<MemberView> {
  return request<MemberView>(`/members/${memberId}/packages`, {
    method: "POST",
    body: JSON.stringify({ requestId }),
  });
}

export function fetchMemberFlows(memberId: number): Promise<AccountFlow[]> {
  return request<AccountFlow[]>(`/members/${memberId}/flows`);
}

export function fetchMachines(): Promise<Machine[]> {
  return request<Machine[]>("/machines");
}

export function fetchActiveSessions(): Promise<MachineSession[]> {
  return request<MachineSession[]>("/sessions/active");
}

export function powerOn(payload: {
  memberId: number;
  machineId: number;
  plannedHours: number;
  requestId: string;
}): Promise<SessionResult> {
  return request<SessionResult>("/sessions/power-on", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export function powerOff(sessionId: number): Promise<SessionResult> {
  return request<SessionResult>("/sessions/power-off", {
    method: "POST",
    body: JSON.stringify({ sessionId }),
  });
}
