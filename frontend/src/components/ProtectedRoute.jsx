import { Navigate } from "react-router-dom";
import { useAuthStore } from "../store/auth.store";

function ProtectedRoute({ children }) {
	const { token } = useAuthStore();
	const storedToken = localStorage.getItem("token");

	if (!token && !storedToken) {
		return <Navigate to="/login" replace />;
	}

	return children;
}

export default ProtectedRoute;
