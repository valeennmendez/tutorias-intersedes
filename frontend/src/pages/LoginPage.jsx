import { Lock, Mail } from "lucide-react";
import { useNavigate } from "react-router-dom";

function LoginPage() {
	const navigate = useNavigate();

	return (
		<div className="bg-[#F7F9FB] h-screen w-screen">
			<div className="flex flex-col w-full h-full justify-center items-center gap-8">
				<div className="flex flex-col items-center">
					<div>
						<img src="/logo-unnoba.png" alt="logo-unnoba" className="rounded-xl w-30" />
					</div>
					<div className="text-center">
						<h1 className="font-bold text-2xl">Sistema de Tutorías</h1>
						<h2 className="font-normal text-slate-600 text-sm">Universidad Nacional del Noroeste de la Provincia de Buenos Aires</h2>
					</div>
				</div>
				<div className="bg-white rounded-lg shadow-md w-120 min-h-80 py-5">
					<div className="flex flex-col items-center">
						<h1 className="font-semibold text-xl">Iniciar Sesión</h1>
						<h2 className="text-slate-600 font-medium text-sm">Ingresa con tu correo institucional UNNOBA</h2>
					</div>
					<div className="px-5 mt-4">
						<form action="">
							<div className="flex  flex-col gap-4">
								<div className="flex flex-col relative ">
									<span className="font-semibold text-slate-800 text-md">Correo Institucional</span>
									<Mail className="absolute bottom-1.5 left-4 size-5 text-slate-700" />
									<input
										type="email"
										className="border shadow-sm font-medium border-slate-300 rounded-md h-8.5 pl-12"
										placeholder="usuario@comunidad.unnoba.edu.ar"
									/>
								</div>
								<div className="flex flex-col relative">
									<span className="font-semibold text-slate-800 text-md">Contraseña</span>
									<Lock className="absolute bottom-1.5 left-4 size-5 text-slate-700" />
									<input
										type="password"
										className="border shadow-sm font-medium border-slate-300 rounded-md h-8.5 pl-12"
										placeholder="Tu contraseña"
									/>
								</div>
							</div>
							<div>
								<button type="submit" className="h-8.5 mt-4 w-full font-medium rounded-md bg-[#008BBA] text-white">
									Iniciar Sesion
								</button>
							</div>
						</form>
						<div className="w-full flex justify-center mt-5  text-center">
							<h3 className="text-slate-600 text-sm font-medium">
								¿No tienes cuenta?{" "}
								<span className="text-[#008BBA]">
									<a href="" onClick={() => navigate("/signup")}>
										Registrate
									</a>{" "}
								</span>
							</h3>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
}

export default LoginPage;
