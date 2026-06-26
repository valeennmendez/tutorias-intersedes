import { create } from "zustand";
import { axiosInstance } from "../utils/axios";
import toast from "react-hot-toast";

const LEIDOS_KEY = "notificaciones_leidas";

const cargarLeidas = () => {
	try {
		const stored = localStorage.getItem(LEIDOS_KEY);
		return stored ? JSON.parse(stored) : [];
	} catch {
		return [];
	}
};

export const useAvisosStore = create((set) => ({
	avisos: [],
	loading: false,
	notificaciones: [],
	notificacionesLoading: false,
	notificacionesLeidas: cargarLeidas(),
	notificacionesVersion: 0,

	marcarLeidas: (ids) => {
		set((state) => {
			const nuevas = [...new Set([...state.notificacionesLeidas, ...ids])];
			localStorage.setItem(LEIDOS_KEY, JSON.stringify(nuevas));
			return { notificacionesLeidas: nuevas };
		});
	},

	reiniciarNoLeidas: () => {
		localStorage.removeItem(LEIDOS_KEY);
		set({ notificacionesLeidas: [] });
	},

	crearAviso: async (data) => {
		try {
			const res = await axiosInstance.post("/avisos", data);
			if (res.status === 201) {
				toast.success("Aviso creado correctamente");
				set((state) => ({ notificacionesVersion: state.notificacionesVersion + 1 }));
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

	fetchNotificaciones: async () => {
		set({ notificacionesLoading: true });
		try {
			const res = await axiosInstance.get("/avisos/mis-avisos");
			set({ notificaciones: res.data, notificacionesLoading: false });
			return res.data;
		} catch (error) {
			if (error.response?.status === 403) {
				set({ notificaciones: [], notificacionesLoading: false });
				return [];
			}
			console.error("Error al obtener notificaciones:", error);
			set({ notificacionesLoading: false });
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
			set((state) => ({ notificacionesVersion: state.notificacionesVersion + 1 }));
			return true;
		} catch (error) {
			toast.error("Error al eliminar el aviso");
			return false;
		}
	},
}));
