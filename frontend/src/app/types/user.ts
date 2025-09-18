// types/user.ts
export interface User {
  id?: number;
  username: string;
  email: string;
  password?: string;
  inventory: string[];
}