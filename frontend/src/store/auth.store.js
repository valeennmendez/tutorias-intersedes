import { create } from "zustand";
import { axiosInstance } from "../utils/axios";

export const useAuthStore = create(() => ({
	registrarUsuario: async (data) => {
		try {
			const res = await axiosInstance.post("/auth/register", data);
			console.log(res);
		} catch (error) {
			console.log("Error al registrar usuario: ", error);
		}
	},
}));
