import { Routes, Route } from "react-router-dom";
import { Toaster } from "react-hot-toast";
import { useEffect } from "react";
import LoginPage from "./pages/LoginPage";
import SignUpPage from "./pages/SignUpPage";
import PruebaPage from "./pages/PruebaPage";
import Home from "./pages/Home/Home";
import PostulacionTutorClient from "./pages/PostulacionTutor/PostulacionTutorClient";
import FiltroTutorPage from "./pages/FiltroTutorPage";
import ValidacionTutorPage from "./pages/ValidacionTutorPage";
import FeedbackTutorPage from "./pages/FeedbackTutor";
import ProtecetedRoute from "./components/ProtectedRoute";
import CrearTutoriaPage from "./pages/CrearTutoriaPage";
import LayoutNavbar from "./components/layouts/LayoutNavbar";
import GestionarAvisos from "./pages/GestionarAvisos/GestionarAvisos";
import BandejaAvisos from "./pages/BandejaAvisos/BandejaAvisos";
import { MisInscripciones } from "./pages/Inscripciones/Inscripciones";
import { useAuthStore } from "./store/auth.store";

function App() {
	const initializeAuth = useAuthStore((state) => state.initializeAuth);

	useEffect(() => {
		initializeAuth();
	}, [initializeAuth]);

	return (
		<div className="light bg-white text-black min-h-screen" style={{ colorScheme: "light" }}>
			<Routes>
				{/* Rutas públicas */}
				<Route path="/login" element={<LoginPage />} />
				<Route path="/signup" element={<SignUpPage />} />
				<Route path="/validacion-tutores" element={<ValidacionTutorPage/>} />
				<Route path="/filtros-tutores" element={<FiltroTutorPage/>} />			<Route path="/feedback-tutores" element={<FeedbackTutorPage/>} />
				{/* Rutas protegidas agrupadas */}
				<Route element={<ProtecetedRoute />}>
					<Route element={<LayoutNavbar />}>
						<Route path="/dashboard" element={<Home />} />
						<Route path="/prueba" element={<PruebaPage />} />
						<Route path="/postulacion-tutor" element={<PostulacionTutorClient />} />
						<Route path="/crear-tutoria" element={<CrearTutoriaPage />} />
						<Route path="/dashboard/gestionar-avisos" element={<GestionarAvisos />} />
						<Route path="/dashboard/bandeja-avisos" element={<BandejaAvisos />} />
						<Route path="/dashboard/mis-inscripciones" element={<MisInscripciones />} />
					</Route>
				</Route>
			</Routes>

			<Toaster />
		</div>
	);
}

export default App;
