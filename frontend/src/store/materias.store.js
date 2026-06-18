import { create } from "zustand";
import { axiosInstance } from "../utils/axios";

export const materiaStore = create((set) => ({
	materiasTutor: [],

	obtenerMateriasTutor: async (id) => {
		try {
			const res = axiosInstance.get(`/materias/tutor/${id}`);
			console.log("Res Materias Tutor: ", res);
			set({ materiasTutor: res.data });
		} catch (error) {
			console.log("Ocurrio un error al obtener las materias del tutor: ", error);
		}
	},
}));
