import { useMemo, useState } from "react";
import { MessageCircle, Send, Star, Users } from "lucide-react";

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";

import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";

const EMAIL_EJEMPLO = "usuario.ejemplo@gmail.com";

const PROFESORES_DISPONIBLES = [
	{
		id: 1,
		nombre: "Juan Pérez",
		carrera: "Licenciatura en Sistemas",
		sede: "Junín",
		materias: ["Bases de Datos", "Modelo Entidad-Relación"],
	},
	{
		id: 2,
		nombre: "Ana Gómez",
		carrera: "Ingeniería Informática",
		sede: "Pergamino",
		materias: ["Programación II", "Estructuras de Datos"],
	},
	{
		id: 3,
		nombre: "Martín López",
		carrera: "Ingeniería en Computación",
		sede: "Junín",
		materias: ["Álgebra Lineal", "Matemática Discreta"],
	},
	{
		id: 4,
		nombre: "Lucía Fernández",
		carrera: "Ingeniería Industrial",
		sede: "Pellegrini",
		materias: ["Cálculo I", "Análisis Matemático"],
	},
];

const FEEDBACK_INICIAL = [
	{
		id: 1,
		profesorId: 1,
		profesor: "Juan Pérez",
		materia: "Bases de Datos",
		calificacion: 5,
		comentario: "Explica con mucha claridad y usa ejemplos concretos. Hace que la materia se entienda mejor.",
		fecha: "2026-06-15",
		esAnonimo: true,
	},
	{
		id: 2,
		profesorId: 1,
		profesor: "Juan Pérez",
		materia: "Modelo Entidad-Relación",
		calificacion: 4,
		comentario: "Muy ordenado para dar la clase y siempre conecta teoría con ejercicios prácticos.",
		fecha: "2026-06-12",
		esAnonimo: true,
	},
	{
		id: 3,
		profesorId: 2,
		profesor: "Ana Gómez",
		materia: "Programación II",
		calificacion: 4,
		comentario: "Acompaña bien los ejercicios y corrige los errores con paciencia. Muy útil para practicar.",
		fecha: "2026-06-14",
		esAnonimo: true,
	},
];

export default function FeedbackTutorPage() {
	const [profesorSeleccionado, setProfesorSeleccionado] = useState("1");
	const [materiaSeleccionada, setMateriaSeleccionada] = useState("Bases de Datos");
	const [calificacion, setCalificacion] = useState(5);
	const [comentario, setComentario] = useState("");
	const [feedback, setFeedback] = useState(FEEDBACK_INICIAL);
	const [enviando, setEnviando] = useState(false);

	const profesorActivo = useMemo(
		() => PROFESORES_DISPONIBLES.find((profesor) => profesor.id.toString() === profesorSeleccionado) || PROFESORES_DISPONIBLES[0],
		[profesorSeleccionado],
	);

	const feedbackDelProfesor = useMemo(
		() => feedback.filter((item) => item.profesorId === Number(profesorActivo.id)),
		[feedback, profesorActivo.id],
	);

	const promedioProfesor = useMemo(() => {
		if (feedbackDelProfesor.length === 0) return "0.0";

		const suma = feedbackDelProfesor.reduce((acumulado, item) => acumulado + item.calificacion, 0);

		return (suma / feedbackDelProfesor.length).toFixed(1);
	}, [feedbackDelProfesor]);

	const handleProfesorChange = (event) => {
		const nuevoProfesorId = event.target.value;
		const profesorNuevo = PROFESORES_DISPONIBLES.find((profesor) => profesor.id.toString() === nuevoProfesorId);

		setProfesorSeleccionado(nuevoProfesorId);
		setMateriaSeleccionada(profesorNuevo?.materias?.[0] || "");
	};

	const handleEnviarFeedback = (event) => {
		event.preventDefault();

		if (!profesorSeleccionado || !materiaSeleccionada || !comentario.trim()) {
			alert("Por favor completa todos los campos");
			return;
		}

		setEnviando(true);

		setTimeout(() => {
			const nuevoFeedback = {
				id: Date.now(),
				profesorId: Number(profesorSeleccionado),
				profesor: profesorActivo.nombre,
				materia: materiaSeleccionada,
				calificacion: Number(calificacion),
				comentario: comentario.trim(),
				fecha: new Date().toISOString().split("T")[0],
				esAnonimo: true,
			};

			setFeedback((feedbackActual) => [nuevoFeedback, ...feedbackActual]);
			setComentario("");
			setCalificacion(5);
			setMateriaSeleccionada(profesorActivo.materias[0] || "");
			setEnviando(false);

			alert("¡Tu reseña anónima fue enviada correctamente!");
		}, 800);
	};

	const renderarEstrellas = (cantidad) => {
		return (
			<div className="flex gap-1">
				{[1, 2, 3, 4, 5].map((star) => (
					<Star
						key={star}
						className={`h-4 w-4 ${star <= cantidad ? "fill-yellow-400 text-yellow-400" : "fill-slate-200 text-slate-200"}`}
					/>
				))}
			</div>
		);
	};

	return (
		<div className="min-h-screen bg-[#F7F9FB]">
			<div className="px-4 py-10">
				<div className="mx-auto flex w-full max-w-5xl flex-col gap-6">
					<div className="flex flex-col gap-2 text-center sm:text-left">
						<div className="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-sky-100 sm:mx-0">
							<MessageCircle className="h-6 w-6 text-[#008BBA]" />
						</div>

						<h1 className="text-3xl font-bold text-slate-900">Feedback por profesor</h1>
						<p className="text-sm text-slate-600">
							Elegí un profesor en particular, revisá sus materias y leé solo sus reseñas. Las opiniones se publican de forma
							anónima.
						</p>
					</div>

					<div className="grid gap-6 lg:grid-cols-2">
						<Card className="border-slate-200 shadow-md lg:order-1">
							<CardHeader>
								<CardTitle className="text-xl text-slate-900">Dejar tu reseña anónima</CardTitle>
								<CardDescription>Contá cómo enseña este profesor y cómo fue su clase.</CardDescription>
							</CardHeader>

							<CardContent className="space-y-4 pt-0">
								<form onSubmit={handleEnviarFeedback} className="space-y-4">
									<div className="space-y-2">
										<label className="text-sm font-semibold text-slate-900">Selecciona el profesor</label>
										<select
											value={profesorSeleccionado}
											onChange={handleProfesorChange}
											className="h-10 w-full rounded-lg border border-slate-300 bg-white px-3 text-slate-900 shadow-sm outline-none transition focus:border-[#008BBA] focus:ring-2 focus:ring-[#008BBA]/20"
										>
											{PROFESORES_DISPONIBLES.map((profesor) => (
												<option key={profesor.id} value={profesor.id}>
													{profesor.nombre} - {profesor.carrera}
												</option>
											))}
										</select>
									</div>

									<div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
										<div className="flex items-start gap-3">
											<div className="flex h-10 w-10 items-center justify-center rounded-full bg-sky-100">
												<Users className="h-5 w-5 text-[#008BBA]" />
											</div>
											<div className="space-y-1">
												<p className="font-semibold text-slate-900">{profesorActivo.nombre}</p>
												<p className="text-sm text-slate-600">{profesorActivo.carrera}</p>
												<p className="text-xs text-slate-500">Sede {profesorActivo.sede}</p>
											</div>
										</div>

										<div className="mt-4 flex flex-wrap gap-2">
											{profesorActivo.materias.map((materia) => (
												<button
													key={materia}
													type="button"
													onClick={() => setMateriaSeleccionada(materia)}
													className={`rounded-full px-3 py-1 text-xs font-medium transition ${
														materiaSeleccionada === materia
															? "bg-[#008BBA] text-white"
															: "bg-white text-slate-700 hover:bg-slate-100"
													}`}
												>
													{materia}
												</button>
											))}
										</div>
									</div>

									<div className="space-y-2">
										<label className="text-sm font-semibold text-slate-900">Materia evaluada</label>
										<select
											value={materiaSeleccionada}
											onChange={(event) => setMateriaSeleccionada(event.target.value)}
											className="h-10 w-full rounded-lg border border-slate-300 bg-white px-3 text-slate-900 shadow-sm outline-none transition focus:border-[#008BBA] focus:ring-2 focus:ring-[#008BBA]/20"
										>
											{profesorActivo.materias.map((materia) => (
												<option key={materia} value={materia}>
													{materia}
												</option>
											))}
										</select>
									</div>

									<div className="space-y-2">
										<label className="text-sm font-semibold text-slate-900">Calificación</label>
										<div className="flex items-center gap-4">
											<div className="flex gap-1">
												{[1, 2, 3, 4, 5].map((star) => (
													<button
														key={star}
														type="button"
														onClick={() => setCalificacion(star)}
														className="transition hover:scale-125"
													>
														<Star
															className={`h-6 w-6 cursor-pointer ${
																star <= calificacion ? "fill-yellow-400 text-yellow-400" : "fill-slate-200 text-slate-200"
															}`}
														/>
													</button>
												))}
											</div>
											<span className="text-sm font-medium text-slate-700">{calificacion} / 5</span>
										</div>
									</div>

									<div className="space-y-2">
										<label className="text-sm font-semibold text-slate-900">Tu comentario anónimo</label>
										<Textarea
											value={comentario}
											onChange={(event) => setComentario(event.target.value)}
											placeholder="Contá cómo enseña, si explica claro, cómo organiza la clase y qué mejorarías."
											className="min-h-24 resize-none rounded-lg border border-slate-300 bg-white px-3 py-2 text-slate-900 shadow-sm outline-none transition focus:border-[#008BBA] focus:ring-2 focus:ring-[#008BBA]/20"
										/>
									</div>

									<Button type="submit" disabled={enviando} className="w-full bg-[#008BBA] text-white hover:bg-[#00779f]">
										<Send className="mr-2 h-4 w-4" />
										{enviando ? "Enviando..." : "Publicar reseña anónima"}
									</Button>
								</form>
							</CardContent>
						</Card>

						<div className="space-y-4 lg:order-2">
							<div className="rounded-xl bg-sky-50 px-4 py-3 text-sm text-sky-900">
								<p className="font-semibold">La reseña es anónima</p>
								<p className="mt-1 text-xs">
									No se muestra tu nombre ni tu correo. Solo aparece la opinión sobre el profesor y la materia.
								</p>
							</div>

							<Card className="border-slate-200 shadow-md">
								<CardHeader>
									<CardTitle className="text-lg text-slate-900">Reseñas de {profesorActivo.nombre}</CardTitle>
									<CardDescription>{profesorActivo.materias.join(" · ")}</CardDescription>
								</CardHeader>

								<CardContent className="space-y-4 pt-0">
									<div className="grid gap-3 sm:grid-cols-3">
										<div className="rounded-xl border border-slate-200 bg-slate-50 p-3">
											<p className="text-xs text-slate-500">Promedio</p>
											<p className="text-2xl font-bold text-slate-900">{promedioProfesor}</p>
										</div>

										<div className="rounded-xl border border-slate-200 bg-slate-50 p-3">
											<p className="text-xs text-slate-500">Reseñas</p>
											<p className="text-2xl font-bold text-slate-900">{feedbackDelProfesor.length}</p>
										</div>

										<div className="rounded-xl border border-slate-200 bg-slate-50 p-3">
											<p className="text-xs text-slate-500">Visibilidad</p>
											<p className="text-sm font-semibold text-slate-900">Anónimo</p>
										</div>
									</div>

									{feedbackDelProfesor.length === 0 ? (
										<p className="text-sm text-slate-600">Aún no hay reseñas para este profesor. ¡Sé el primero!</p>
									) : (
										feedbackDelProfesor.map((item) => (
											<div key={item.id} className="space-y-2 border-b border-slate-200 pb-4 last:border-0">
												<div className="flex items-start justify-between gap-3">
													<div>
														<p className="font-semibold text-slate-900">{item.profesor}</p>
														<p className="text-xs text-slate-600">{item.materia}</p>
													</div>

													{renderarEstrellas(item.calificacion)}
												</div>

												<p className="text-sm text-slate-700">{item.comentario}</p>

												<div className="flex items-center justify-between text-xs text-slate-500">
													<span>Publicada de forma anónima</span>
													<span>{item.fecha}</span>
												</div>
											</div>
										))
									)}
								</CardContent>
							</Card>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
}
