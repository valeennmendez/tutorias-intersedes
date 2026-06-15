import { Routes, Route } from "react-router-dom";
import PruebaPage from "./pages/PruebaPage";
import LoginPage from "./pages/LoginPage";
import SignUpPage from "./pages/SignUpPage";
import PostulacionTutorClient from "./pages/PostulacionTutor/PostulacionTutorClient";
import { Toaster } from "react-hot-toast";

function App() {
	return (
		<div className="light bg-white text-black min-h-screen" style={{ colorScheme: "light" }}>
			<Routes>
				<Route path="/prueba" element={<PruebaPage />} />
				<Route path="/login" element={<LoginPage />} />
				<Route path="/signup" element={<SignUpPage />} />
				<Route path="/postulacion-tutor" element={<PostulacionTutorClient />} />
			</Routes>

			<Toaster />
		</div>
	);
}

export default App;
