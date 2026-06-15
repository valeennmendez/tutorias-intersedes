import axios from "axios";

export const axiosInstance = axios.create({
	baseURL: "http://localhost:8080",
	withCredentials: true,
});

const token = localStorage.getItem("token");
if (token) {
	axiosInstance.defaults.headers.common["Authorization"] = `Bearer ${token}`;
}
