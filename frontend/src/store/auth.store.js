import { create } from "zustand";
import { axiosInstance } from "../utils/axios";
import toast from "react-hot-toast";

export const useAuthStore = create(() => ({
	registrarUsuario: async (data) => {
		try {
			const res = await axiosInstance.post("/auth/register", data);
			console.log(res);
			toast.success(res.data);
		} catch (error) {
			const backendError = error.response?.data;

			if (typeof backendError === "string") {
				// Caso simple: mensaje directo
				toast.error(backendError);
			} else if (typeof backendError === "object") {
				// Caso múltiple: recorrer las claves y mostrar cada error
				Object.values(backendError).forEach((msg) => {
					toast.error(msg);
				});
			} else {
				toast.error("Error desconocido al registrar usuario");
			}
		}
	},
}));
