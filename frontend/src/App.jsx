import { Routes, Route } from "react-router-dom";
import PruebaPage from "./pages/PruebaPage";
import LoginPage from "./pages/LoginPage";

function App() {
	return (
		<Routes>
			<Route path="/prueba" element={<PruebaPage />} />
			<Route path="/login" element={<LoginPage />} />
		</Routes>
	);
}

export default App;
