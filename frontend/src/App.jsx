import { Routes, Route } from "react-router-dom";
import { Toaster } from "react-hot-toast";
import LoginPage from "./pages/LoginPage";
import SignUpPage from "./pages/SignUpPage";
import PruebaPage from "./pages/PruebaPage";
import PostulacionTutorClient from "./pages/PostulacionTutor/PostulacionTutorClient";
import FiltroTutorPage from "./pages/FiltroTutorPage";
import ValidacionTutorPage from "./pages/ValidacionTutorPage";
import FeedbackTutorPage from "./pages/FeedbackTutor";
import ProtecetedRoute from "./components/ProtectedRoute";

function App() {
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
					<Route path="/prueba" element={<PruebaPage />} />
					<Route path="/postulacion-tutor" element={<PostulacionTutorClient />} />
				</Route>
			</Routes>

			<Toaster />
		</div>
	);
}

export default App;
