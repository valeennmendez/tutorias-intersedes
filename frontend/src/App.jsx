import { Routes, Route } from "react-router-dom";
import PruebaPage from "./pages/PruebaPage";
import LoginPage from "./pages/LoginPage";
import SignUpPage from "./pages/SignUpPage";

function App() {
	return (
		<Routes>
			<Route path="/prueba" element={<PruebaPage />} />
			<Route path="/login" element={<LoginPage />} />
			<Route path="/signup" element={<SignUpPage />} />
		</Routes>
	);
}

export default App;
