import { API_BASE_URL } from "../constants/app";
import type {
  Member,
  MemberProfile,
  OverviewResponse,
  SeatView,
  StartSessionResult,
  StartSessionPayload,
  StopSessionResult,
} from "../types";

export class ApiError extends Error {
  readonly status: number;

  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: { Accept: "application/json", "Content-Type": "application/json" },
    ...options,
  });

  if (response.status === 204) {
    return undefined as T;
  }

  let body: unknown;
  try {
    body = await response.json();
  } catch {
    body = {};
  }

  if (!response.ok) {
    const message =
      body && typeof body === "object" && "message" in body
        ? String((body as { message: unknown }).message)
        : `请求失败（${response.status}）`;
    throw new ApiError(response.status, message);
  }

  return body as T;
}

export async function fetchOverview(): Promise<OverviewResponse> {
  return request<OverviewResponse>("/overview", { method: "GET" });
}

export async function fetchSeats(): Promise<SeatView[]> {
  return request<SeatView[]>("/seats", { method: "GET" });
}

export async function fetchMember(phone: string): Promise<MemberProfile> {
  return request<MemberProfile>(`/members?phone=${encodeURIComponent(phone)}`, { method: "GET" });
}

export async function createMember(phone: string, name: string): Promise<Member> {
  return request<Member>("/members", {
    method: "POST",
    body: JSON.stringify({ phone, name: name || null }),
  });
}

export async function rechargeMember(phone: string, amount: number): Promise<MemberProfile> {
  return request<MemberProfile>(`/members/${encodeURIComponent(phone)}/recharge`, {
    method: "POST",
    body: JSON.stringify({ amount }),
  });
}

export async function buyPackage(phone: string): Promise<MemberProfile> {
  return request<MemberProfile>(`/members/${encodeURIComponent(phone)}/packages`, {
    method: "POST",
  });
}

export async function startSession(payload: StartSessionPayload): Promise<StartSessionResult> {
  return request<StartSessionResult>("/sessions/start", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export async function stopSession(sessionId: number): Promise<StopSessionResult> {
  return request<StopSessionResult>(`/sessions/${sessionId}/stop`, { method: "POST" });
}
