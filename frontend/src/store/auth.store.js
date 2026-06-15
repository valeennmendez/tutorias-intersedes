import { create } from "zustand";
import { axiosInstance } from "../utils/axios";
import toast from "react-hot-toast";

export const useAuthStore = create(() => ({
	token: null,

	login: async (data) => {
		try {
			const res = await axiosInstance.post("/auth/login", data);

			if (res.status === 200) {
				toast.success("Login exitoso.");
			}

			const token = res.data;

			localStorage.setItem("token", token);

			console.log("token: ", token);

			axiosInstance.defaults.headers.common["Authorization"];
			return res.status;
		} catch (error) {
			console.log("Error al iniciar sesion", error.response?.data);
			toast.error("El correo y/o contaseña son incorrectos");
		}
	},

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
