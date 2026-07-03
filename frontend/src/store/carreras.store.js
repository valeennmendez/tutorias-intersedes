import { create } from "zustand";
import { axiosInstance } from "../utils/axios";

export const useCarrerasStore = create((set) => ({
	carreras: [],
	loading: false,

	fetchCarreras: async () => {
		set({ loading: true });
		try {
			const res = await axiosInstance.get("/carreras");
			set({ carreras: res.data });
		} catch (error) {
			console.log("Error al obtener carreras", error.response ? error.response.data : error);
		} finally {
			set({ loading: false });
		}
	},
}));
