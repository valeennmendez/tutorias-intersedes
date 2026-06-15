import ProtectedRoute from "./components/ProtectedRoute";
import { Toaster } from "react-hot-toast";
import { Routes, Route } from "react-router-dom";
import PruebaPage from "./pages/PruebaPage";
import LoginPage from "./pages/LoginPage";
import SignUpPage from "./pages/SignUpPage";
import PostulacionTutorClient from "./pages/PostulacionTutor/PostulacionTutorClient";

function App() {
	return (
		<div className="light bg-white text-black min-h-screen" style={{ colorScheme: "light" }}>
			<Routes>
				{/* Rutas públicas */}
				<Route path="/login" element={<LoginPage />} />
				<Route path="/signup" element={<SignUpPage />} />

				{/* Rutas protegidas */}
				<Route path="/prueba"element={<ProtectedRoute><PruebaPage /></ProtectedRoute>}/>
				<Route path="/postulacion-tutor" element={<ProtectedRoute>
							<PostulacionTutorClient />
						</ProtectedRoute>
					}
				/>
			</Routes>

			<Toaster />
		</div>
	);
}

export default App;
