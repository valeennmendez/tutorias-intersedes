import { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { MessageCircle, Star } from "lucide-react";

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { axiosInstance } from "@/utils/axios";

export default function FeedbackTutorPage() {
    const [searchParams] = useSearchParams();
    const [inscripciones, setInscripciones] = useState([]);
    const [cargandoDatos, setCargandoDatos] = useState(true);

    const [inscripcionSeleccionadaId, setInscripcionSeleccionadaId] = useState("");
    const [calificacion, setCalificacion] = useState(5);
    const [comentario, setComentario] = useState("");
    
    const [feedbackList, setFeedbackList] = useState([]); 
    const [cargandoFeedback, setCargandoFeedback] = useState(false);
    const [enviando, setEnviando] = useState(false);
    const [tutorIdActivo, setTutorIdActivo] = useState(null);

    // 1. Cargar las inscripciones válidas desde la base de datos
    useEffect(() => {
        const preSeleccionada = searchParams.get("inscripcionId");

        const cargarMisInscripciones = async () => {
            try {
                setCargandoDatos(true);
                const response = await axiosInstance.get("/inscripciones/mis-inscripciones"); 
                const lista = response.data || [];
                setInscripciones(lista);

                if (preSeleccionada && lista.some((i) => i.id.toString() === preSeleccionada)) {
                    setInscripcionSeleccionadaId(preSeleccionada);
                } else if (lista.length > 0) {
                    setInscripcionSeleccionadaId(lista[0].id.toString());
                }
            } catch (error) {
                console.error("Error al conectar con la base de datos:", error);
            } finally {
                setCargandoDatos(false);
            }
        };

        cargarMisInscripciones();
    }, []);

    // Identificar la inscripción activa que el usuario seleccionó
    const inscripcionActiva = useMemo(() => {
        if (inscripciones.length === 0) return null;
        return inscripciones.find((i) => i.id.toString() === inscripcionSeleccionadaId) || inscripciones[0];
    }, [inscripcionSeleccionadaId, inscripciones]);

    // Cuando cambia la inscripción seleccionada, obtener el tutorId desde la tutoría
    useEffect(() => {
        if (!inscripcionActiva?.tutoriaId) {
            setTutorIdActivo(null);
            return;
        }

        const obtenerTutorId = async () => {
            try {
                const response = await axiosInstance.get(`/tutorias/${inscripcionActiva.tutoriaId}`);
                const tutorId = response.data?.tutorId;
                if (tutorId) {
                    setTutorIdActivo(tutorId);
                }
            } catch (error) {
                setTutorIdActivo(null);
            }
        };

        obtenerTutorId();
    }, [inscripcionActiva]);

    // 2. Cargar feedbacks del tutor seleccionado
    const cargarFeedbackDelTutor = async (idTutor) => {
        if (!idTutor) return;
        setCargandoFeedback(true);
        try {
            const response = await axiosInstance.get(`/feedback/tutor/${idTutor}`);
            const items = response.data?.content || response.data || [];
            setFeedbackList(items);
        } catch (error) {
            setFeedbackList([]);
        } finally {
            setCargandoFeedback(false);
        }
    };

    useEffect(() => {
        if (tutorIdActivo) {
            cargarFeedbackDelTutor(tutorIdActivo);
        }
    }, [tutorIdActivo]);

    const handleEnviarFeedback = (event) => {
        event.preventDefault();

        if (!inscripcionSeleccionadaId) {
            alert("Por favor selecciona un profesor del listado.");
            return;
        }

        if (!comentario.trim()) {
            alert("Por favor escribe un comentario antes de enviar.");
            return;
        }

        setEnviando(true);

        axiosInstance
            .post(`/feedback/inscripcion/${inscripcionSeleccionadaId}`, {
                calificacion: Number(calificacion),
                comentarios: comentario.trim(),
                esAnonimo: true
            })
            .then(async () => {
                if (tutorIdActivo) {
                    await cargarFeedbackDelTutor(tutorIdActivo);
                }
                setComentario("");
                setCalificacion(5);
                alert("¡Tu reseña anónima fue enviada correctamente!");
            })
            .catch((error) => {
                const mensaje = error.response?.data?.error || "Error al guardar el feedback en la base de datos.";
                alert(mensaje);
            })
            .finally(() => {
                setEnviando(false);
            });
    };

    const promedioTutor = useMemo(() => {
        if (!feedbackList || feedbackList.length === 0) return "0.0";
        const suma = feedbackList.reduce((acc, item) => acc + (item.calificacion || 0), 0);
        return (suma / feedbackList.length).toFixed(1);
    }, [feedbackList]);

    if (cargandoDatos) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-[#F7F9FB]">
                <p className="text-lg font-medium text-slate-600">Sincronizando con el servidor académico...</p>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-[#F7F9FB]">
            <div className="px-4 py-10">
                <div className="mx-auto flex w-full max-w-5xl flex-col gap-6">
                    <div className="flex flex-col gap-2 text-center sm:text-left">
                        <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-sky-100 sm:mx-0">
                            <MessageCircle className="h-6 w-6 text-[#008BBA]" />
                        </div>
                        <h1 className="text-3xl font-bold text-slate-900">Evaluar mis Profesores</h1>
                        <p className="text-sm text-slate-600">
                            Puntúa el desempeño de tus tutores de forma 100% anónima.
                        </p>
                    </div>

                    {inscripciones.length === 0 ? (
                        <div className="rounded-xl border border-slate-200 bg-white p-8 text-center">
                            <p className="text-slate-600">No registrás clases finalizadas disponibles para evaluar en tu cuenta.</p>
                        </div>
                    ) : (
                        <div className="grid gap-6 lg:grid-cols-2">
                            {/* Formulario */}
                            <Card className="border-slate-200 shadow-md">
                                <CardHeader>
                                    <CardTitle className="text-xl text-slate-900">Dejar Reseña</CardTitle>
                                </CardHeader>

                                <CardContent className="space-y-4 pt-0">
                                    <form onSubmit={handleEnviarFeedback} className="space-y-4">
                                        
                                        <div className="space-y-2">
                                            <label className="text-sm font-semibold text-slate-900">Seleccioná al Profesor:</label>
                                            <select
                                                value={inscripcionSeleccionadaId}
                                                onChange={(e) => setInscripcionSeleccionadaId(e.target.value)}
                                                className="h-10 w-full rounded-lg border border-slate-300 bg-white px-3 text-slate-900 shadow-sm outline-none"
                                            >
                                                {inscripciones.map((ins) => (
                                                    <option key={ins.id} value={ins.id}>
                                                        {ins.nombreTutor} {ins.nombreTutoria ? `— ${ins.nombreTutoria}` : ""}
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
                                                                className={`h-6 w-6 ${star <= calificacion ? "fill-yellow-400 text-yellow-400" : "fill-slate-200 text-slate-200"}`}
                                                            />
                                                        </button>
                                                    ))}
                                                </div>
                                                <span className="text-sm font-medium text-slate-700">{calificacion} / 5</span>
                                            </div>
                                        </div>

                                        <div className="space-y-2">
                                            <label className="text-sm font-semibold text-slate-900">Comentario</label>
                                            <Textarea
                                                value={comentario}
                                                onChange={(e) => setComentario(e.target.value)}
                                                placeholder="Contá qué te pareció la clase..."
                                                className="min-h-24 resize-none rounded-lg border border-slate-300"
                                            />
                                        </div>

                                        <Button type="submit" disabled={enviando} className="w-full bg-[#008BBA] text-white">
                                            {enviando ? "Enviando..." : "Enviar feedback"}
                                        </Button>
                                    </form>
                                </CardContent>
                            </Card>

                            {/* Historial */}
                            <div className="space-y-4">
                                <Card className="border-slate-200 shadow-md">
                                    <CardHeader>
                                        <CardTitle className="text-lg text-slate-900">Historial de Reseñas</CardTitle>
                                    </CardHeader>

                                    <CardContent className="space-y-4 pt-0">
                                        <div className="grid grid-cols-2 gap-2">
                                            <div className="rounded-xl border border-slate-200 bg-slate-50 p-3 text-center">
                                                <p className="text-xs text-slate-500">Promedio</p>
                                                <p className="text-xl font-bold text-slate-900">{promedioTutor}</p>
                                            </div>
                                            <div className="rounded-xl border border-slate-200 bg-slate-50 p-3 text-center">
                                                <p className="text-xs text-slate-500">Total Reseñas</p>
                                                <p className="text-xl font-bold text-slate-900">{feedbackList.length}</p>
                                            </div>
                                        </div>

                                        {cargandoFeedback ? (
                                            <p className="text-sm text-slate-600">Cargando opiniones...</p>
                                        ) : feedbackList.length === 0 ? (
                                            <p className="text-sm text-slate-600">Este profesor aún no tiene comentarios registrados en el sistema.</p>
                                        ) : (
                                            feedbackList.map((item) => (
                                                <div key={item.id} className="space-y-2 border-b border-slate-200 pb-3 last:border-0">
                                                    <div className="flex items-start justify-between">
                                                        <p className="font-semibold text-sm text-slate-900">Estudiante Anónimo</p>
                                                        <div className="flex gap-1">
                                                            {[1, 2, 3, 4, 5].map((star) => (
                                                                <Star
                                                                    key={star}
                                                                    className={`h-3 w-3 ${star <= item.calificacion ? "fill-yellow-400 text-yellow-400" : "fill-slate-200 text-slate-200"}`}
                                                                />
                                                            ))}
                                                        </div>
                                                    </div>
                                                    <p className="text-xs text-slate-700">{item.comentarios}</p>
                                                </div>
                                            ))
                                        )}
                                    </CardContent>
                                </Card>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}