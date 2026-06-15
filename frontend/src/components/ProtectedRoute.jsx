import { Navigate, Outlet } from "react-router-dom";
import { useAuthStore } from "../store/auth.store";

function ProtectedRoute() {
	const { token } = useAuthStore();
	const storedToken = localStorage.getItem("token");

	if (!token && !storedToken) {
		return <Navigate to="/login" replace />;
	}

	return <Outlet />;
}

export default ProtectedRoute;
