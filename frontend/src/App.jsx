import { Routes, Route } from "react-router-dom";
import { Toaster } from "react-hot-toast";
import LoginPage from "./pages/LoginPage";
import SignUpPage from "./pages/SignUpPage";
import PruebaPage from "./pages/PruebaPage";
import PostulacionTutorClient from "./pages/PostulacionTutor/PostulacionTutorClient";
import ProtecetedRoute from "./components/ProtectedRoute";
import CrearTutoriaPage from "./pages/CrearTutoriaPage";

function App() {
	return (
		<div className="light bg-white text-black min-h-screen" style={{ colorScheme: "light" }}>
			<Routes>
				{/* Rutas públicas */}
				<Route path="/login" element={<LoginPage />} />
				<Route path="/signup" element={<SignUpPage />} />

				{/* Rutas protegidas agrupadas */}
				<Route element={<ProtecetedRoute />}>
					<Route path="/prueba" element={<PruebaPage />} />
					<Route path="/postulacion-tutor" element={<PostulacionTutorClient />} />
					<Route path="/crear-tutoria" element={<CrearTutoriaPage />} />
				</Route>
			</Routes>

			<Toaster />
		</div>
	);
}

export default App;
