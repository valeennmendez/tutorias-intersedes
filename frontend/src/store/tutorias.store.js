import toast from "react-hot-toast";
import { create } from "zustand";
import { axiosInstance } from "../utils/axios";

export const tutoriaStore = create(() => ({
	crearTutoria: async (data) => {
		try {
			const res = await axiosInstance.post("/tutorias", data);
			console.log("RES: ", res);
			if (res.status === 200) {
				toast.success("Tutoria creada correctamente.");
			}
		} catch (error) {
			(console.log("Ocurrio un error al crear la tutoria: ", error), toast.error("Ocurrió un error al crear la tutoria,"));
		}
	},
}));
