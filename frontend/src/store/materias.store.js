import { create } from "zustand";
import { axiosInstance } from "../utils/axios";

export const materiaStore = create((set) => ({
	materiasTutor: [],

	obtenerMateriasTutor: async () => {
		try {
			const storedUser = localStorage.getItem("user");
			const user = storedUser ? JSON.parse(storedUser) : null;
			const res = await axiosInstance.get(`/materias/tutor/${user.id}`);
			set({ materiasTutor: res.data });
		} catch (error) {
			console.log("Ocurrio un error al obtener las materias del tutor: ", error);
		}
	},
}));
