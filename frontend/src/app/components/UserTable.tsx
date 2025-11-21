import React from "react";
import { User } from "../types/user";
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;


interface Props {
  users: User[];
  setSelectedUser: (user: User) => void;
  refreshUsers: () => void;
}

const UserTable: React.FC<Props> = ({ users, setSelectedUser, refreshUsers }) => {
  const deleteUser = async (id: number) => {
    await fetch(`${API_BASE_URL}/api/users/${id}`, { method: "DELETE" });
    refreshUsers();
  };

  return (
    <div className="mb-4">
      <h2 className="text-xl font-semibold mb-2">Users</h2>
      <table className="table-auto border-collapse border border-gray-400">
        <thead>
          <tr>
            <th className="border p-2">ID</th>
            <th className="border p-2">Username</th>
            <th className="border p-2">Email</th>
            <th className="border p-2">Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td className="border p-2">{user.id}</td>
              <td className="border p-2 cursor-pointer" onClick={() => setSelectedUser(user)}>
                {user.username}
              </td>
              <td className="border p-2">{user.email}</td>
              <td className="border p-2">
                <button
                  className="bg-red-500 text-white px-2 py-1 rounded"
                  onClick={() => deleteUser(user.id!)}
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default UserTable;
