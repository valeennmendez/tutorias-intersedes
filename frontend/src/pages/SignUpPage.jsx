function SignUpPage() {
	return (
		<div className="bg-[#F7F9FB] h-screen w-screen">
			<div className="flex flex-col w-full h-full justify-center items-center gap-8">
				<div className="flex flex-col items-center">
					<div>
						<img src="/logo-unnoba.png" alt="logo-unnoba" className="rounded-xl w-30" />
					</div>
					<div className="text-center">
						<h1 className="font-bold text-2xl">Crear Cuenta</h1>
						<h2 className="font-normal text-slate-600 text-sm">Sistema de Tutorías UNNOBA</h2>
					</div>
				</div>
				<div className="bg-white rounded-lg shadow-md w-120 min-h-80 py-5">
					<div className="flex flex-col items-center">
						<h1 className="font-semibold text-xl">Registro</h1>
						<h2 className="text-slate-600 font-medium text-sm">Completa tus datos para crear tu cuenta</h2>
					</div>
				</div>
			</div>
		</div>
	);
}

export default SignUpPage;
