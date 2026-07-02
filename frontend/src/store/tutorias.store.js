import toast from "react-hot-toast";
import { create } from "zustand";
import { axiosInstance } from "../utils/axios";

export const tutoriaStore = create(() => ({
	crearTutoria: async (data) => {
		try {
			const res = await axiosInstance.post("/tutorias", data);
			if (res.status === 201) {
				toast.success("Tutoria creada correctamente!.");
			}
			return true;
		} catch (error) {
			(console.log("Ocurrio un error al crear la tutoria: ", error.response?.data.detalle),
				toast.error(error.response?.data.detalle));
			return false;
		}
	},
	actualizarTutoria: async (id, data) => {
		try {
			const res = await axiosInstance.put(`/tutorias/${id}`, data);
			if (res.status === 200) {
				toast.success("Tutoria actualizada correctamente.");
			}
			return res.data;
		} catch (error) {
			const mensaje = error.response?.data?.detalle || error.response?.data?.message || "Error al actualizar la tutoria";
			toast.error(mensaje);
			throw error;
		}
	},
	eliminarTutoria: async (id) => {
		try {
			const res = await axiosInstance.delete(`/tutorias/${id}`);
			if (res.status === 204) {
				toast.success("Tutoria eliminada correctamente.");
			}
		} catch (error) {
			const mensaje = error.response?.data?.detalle || error.response?.data?.message || "Error al eliminar la tutoria";
			toast.error(mensaje);
			throw error;
		}
	},
}));
