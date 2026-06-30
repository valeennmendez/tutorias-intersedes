import { create } from "zustand";
import { axiosInstance } from "../utils/axios";
import toast from "react-hot-toast";

export const useAuthStore = create((set) => ({
    token: null,
    user: null,

    login: async(data) => {
        try {
            const res = await axiosInstance.post("/auth/login", data);

            if (res.status === 200) {
                toast.success("Login exitoso.");
            }

            const { token, usuario } = res.data;

            localStorage.setItem("token", token);
            localStorage.setItem("user", JSON.stringify(usuario));

            const userData = {
                id: usuario.id,
                nombre: usuario.nombre,
                apellido: usuario.apellido,
                email: usuario.email,
                role: usuario.role.toLowerCase(),
            };

            set({ token, user: userData });

            axiosInstance.defaults.headers.common["Authorization"] = `Bearer ${token}`;

            return res.status;
        } catch (error) {
            console.log("Error al iniciar sesion", error.response ? error.response.data : data);
            toast.error("El correo y/o contaseña son incorrectos");
        }
    },

    logout: () => {
        set({ token: null, user: null });
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        delete axiosInstance.defaults.headers.common["Authorization"];
    },

    initializeAuth: () => {
        const token = localStorage.getItem("token");
        const user = localStorage.getItem("user");
        if (token && user) {
            const userData = JSON.parse(user);
            if (userData.role) {
                userData.role = userData.role.toLowerCase();
            }
            set({ token, user: userData });
            axiosInstance.defaults.headers.common["Authorization"] = `Bearer ${token}`;
        }
    },

    registrarUsuario: async(data) => {
        try {
            const res = await axiosInstance.post("/auth/register", data);
            console.log(res);
            toast.success(res.data);

            return res.status;
        } catch (error) {
            const backendError = error.response ? error.response.data : data;

            if (typeof backendError === "string") {
                toast.error(backendError);
            } else if (typeof backendError === "object") {
                Object.values(backendError).forEach((msg) => {
                    toast.error(msg);
                });
            } else {
                toast.error("Error desconocido al registrar usuario");
            }
        }
    },
}));