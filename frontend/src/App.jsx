import { Routes, Route } from "react-router-dom";
import PruebaPage from "./pages/PruebaPage";
import { PostulacionTutorClient } from "./components/PostulacionTutor/PostulacionTutorClient";

function App() {
	return (
		<div className="light bg-white text-black min-h-screen" style={{ colorScheme: 'light' }}>
			<Routes>
				<Route path="/prueba" element={<PruebaPage />} />
				<Route path="/postulacion-tutor" element={<PostulacionTutorClient />} />
			</Routes>
		</div>
	);
}

export default App;
