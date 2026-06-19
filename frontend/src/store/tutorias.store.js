import toast from "react-hot-toast";
import { create } from "zustand";
import { axiosInstance } from "../utils/axios";

export const tutoriaStore = create((set) => ({
	crearTutoria: async (data) => {
		try {
			const res = await axiosInstance.post("/tutorias", data);
			console.log("RES: ", res);
		} catch (error) {
			(console.log("Ocurrio un error al crear la tutoria: ", error), toast.error("Ocurrió un error al crear la tutoria,"));
		}
	},
}));
