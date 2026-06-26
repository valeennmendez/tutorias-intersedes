import { create } from "zustand";
import { axiosInstance } from "../utils/axios";
import toast from "react-hot-toast";

export const useAvisosStore = create((set) => ({
	avisos: [],
	loading: false,

	crearAviso: async (data) => {
		try {
			const res = await axiosInstance.post("/avisos", data);
			if (res.status === 201) {
				toast.success("Aviso creado correctamente");
				return res.data;
			}
		} catch (error) {
			toast.error(error.response?.data?.detalle || "Error al crear el aviso");
		}
	},

	getMisAvisos: async () => {
		set({ loading: true });
		try {
			const res = await axiosInstance.get("/avisos/mis-avisos");
			set({ avisos: res.data, loading: false });
			return res.data;
		} catch (error) {
			console.error("Error al obtener avisos:", error);
			set({ loading: false });
		}
	},

	getMisAvisosEnviados: async () => {
		set({ loading: true });
		try {
			const res = await axiosInstance.get("/avisos/mis-avisos-enviados");
			set({ avisos: res.data, loading: false });
			return res.data;
		} catch (error) {
			console.error("Error al obtener avisos enviados:", error);
			set({ loading: false });
		}
	},

	getAvisosByTutoria: async (tutoriaId) => {
		set({ loading: true });
		try {
			const res = await axiosInstance.get(`/avisos/tutoria/${tutoriaId}`);
			set({ loading: false });
			return res.data;
		} catch (error) {
			console.error("Error al obtener avisos de la tutoría:", error);
			set({ loading: false });
		}
	},

	eliminarAviso: async (id) => {
		try {
			await axiosInstance.delete(`/avisos/${id}`);
			toast.success("Aviso eliminado correctamente");
			return true;
		} catch (error) {
			toast.error("Error al eliminar el aviso");
			return false;
		}
	},
}));
