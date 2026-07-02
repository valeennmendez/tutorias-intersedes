import { useState, useEffect, useCallback } from "react";
import { Link } from "react-router-dom";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Checkbox } from "@/components/ui/checkbox";
import {
  Calendar,
  Clock,
  MapPin,
  Video,
  Star,
  Loader2,
  MessageSquare,
  CalendarCheck,
  CalendarX,
} from "lucide-react";
import { format } from "date-fns";
import { es } from "date-fns/locale";
import toast from "react-hot-toast";
import { axiosInstance } from "@/utils/axios";
import { useAuthStore } from "@/store/auth.store";

export function MisInscripciones() {
  const { user } = useAuthStore();
  const [inscripciones, setInscripciones] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [loading, setLoading] = useState(null);
  const [detalleAbierto, setDetalleAbierto] = useState(null);
  const [feedbackDialogOpen, setFeedbackDialogOpen] = useState(false);
  const [selectedInscripcion, setSelectedInscripcion] = useState(null);
  const [feedbackData, setFeedbackData] = useState({
    calificacion: 5,
    comentario: "",
    es_anonimo: false,
  });

  // 1. Carga de datos: reemplaza al Server Component (page.tsx)
  const cargarInscripciones = useCallback(async () => {
    try {
      const res = await axiosInstance.get("/inscripciones/mis-inscripciones");
      setInscripciones(res.data);
    } catch {
      toast.error("No se pudieron cargar las inscripciones");
    } finally {
      setCargando(false);
    }
  }, []);

  useEffect(() => {
    const cargarInicial = async () => {
      try {
        const res = await axiosInstance.get("/inscripciones/mis-inscripciones");
        setInscripciones(res.data);
      } catch {
        toast.error("No se pudieron cargar las inscripciones");
      } finally {
        setCargando(false);
      }
    };

    cargarInicial();
  }, []);

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const proximasInscripciones = inscripciones.filter(
    (i) => i.status === "inscripto" && new Date(i.tutoria.fecha) >= today
  );

  const pasadasInscripciones = inscripciones.filter(
    (i) =>
      i.status === "asistio" ||
      (i.status === "inscripto" && new Date(i.tutoria.fecha) < today)
  );

  const canceladasInscripciones = inscripciones.filter(
    (i) => i.status === "cancelada" || i.status === "no_asistio"
  );

  // 2. Cancelar: ahora llama a tu API en vez de Supabase directo
  const handleCancelar = async (inscripcionId) => {
    setLoading(inscripcionId);
    try {
      await axiosInstance.put(`/inscripciones/tutoria/${inscripcionId}/cancelar`);

      toast.success("Inscripción cancelada");
      await cargarInscripciones();
    } catch {
      toast.error("Error al cancelar la inscripción");
    } finally {
      setLoading(null);
    }
  };

  // 3. Feedback: POST a tu API
  const handleSubmitFeedback = async () => {
    if (!selectedInscripcion) return;

    setLoading("feedback");
    try {
      await axiosInstance.post(`/feedback/inscripcion/${selectedInscripcion}`, {
        calificacion: feedbackData.calificacion,
        comentarios: feedbackData.comentario || "",
        esAnonimo: feedbackData.es_anonimo,
      });

      toast.success("¡Gracias por tu feedback!");
      setFeedbackDialogOpen(false);
      setSelectedInscripcion(null);
      setFeedbackData({ calificacion: 5, comentario: "", es_anonimo: false });
      await cargarInscripciones();
    } catch (error) {
      toast.error(error.response?.data?.error || "Error al enviar feedback");
    } finally {
      setLoading(null);
    }
  };

  const openFeedbackDialog = (inscripcionId) => {
    setSelectedInscripcion(inscripcionId);
    setFeedbackDialogOpen(true);
  };

  const toggleDetalle = (id) => {
    setDetalleAbierto((actual) => (actual === id ? null : id));
  };

  const getStatusBadge = (status, fecha) => {
    const isPast = new Date(fecha) < today;
    if (status === "inscripto" && !isPast) {
      return <Badge className="bg-green-600">Confirmada</Badge>;
    }
    if (status === "inscripto" && isPast) {
      return <Badge variant="secondary">Pendiente feedback</Badge>;
    }
    if (status === "asistio") {
      return <Badge className="bg-blue-600">Asistió</Badge>;
    }
    if (status === "cancelada") {
      return <Badge variant="destructive">Cancelada</Badge>;
    }
    if (status === "no_asistio") {
      return <Badge variant="destructive">No asistió</Badge>;
    }
    return <Badge variant="outline">{status}</Badge>;
  };

  const InscripcionCard = ({ inscripcion, showActions = false }) => {
    const hasFeedback = inscripcion.feedback && inscripcion.feedback.length > 0;
    const isPast = new Date(inscripcion.tutoria.fecha) < today;
    const canLeaveFeedback =
      isPast && !hasFeedback && inscripcion.status !== "cancelada";
    const puedeGestionar =
      Number(inscripcion.tutoria.tutorId) === Number(user?.id) &&
      (user?.role === "tutor" || user?.role === "admin");
    const detalleVisible = detalleAbierto === inscripcion.id;

    return (
      <Card>
        <CardContent className="pt-6">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
            <div className="space-y-2 flex-1">
              <div className="flex items-center gap-2 flex-wrap">
                <h4 className="font-medium">{inscripcion.tutoria.titulo}</h4>
                {getStatusBadge(inscripcion.status, inscripcion.tutoria.fecha)}
                <Badge
                  variant={
                    inscripcion.tutoria.modalidad === "virtual"
                      ? "secondary"
                      : "outline"
                  }
                >
                  {inscripcion.tutoria.modalidad === "virtual" ? (
                    <Video className="h-3 w-3 mr-1" />
                  ) : (
                    <MapPin className="h-3 w-3 mr-1" />
                  )}
                  {inscripcion.tutoria.modalidad}
                </Badge>
              </div>
              <p className="text-sm text-muted-foreground">
                {inscripcion.tutoria.materia.nombre} -{" "}
                {inscripcion.tutoria.tutor.nombre}{" "}
                {inscripcion.tutoria.tutor.apellido}
              </p>
              <div className="flex items-center gap-4 text-sm text-muted-foreground">
                <span className="flex items-center gap-1">
                  <Calendar className="h-4 w-4" />
                  {format(new Date(inscripcion.tutoria.fecha), "d 'de' MMMM, yyyy", {
                    locale: es,
                  })}
                </span>
                <span className="flex items-center gap-1">
                  <Clock className="h-4 w-4" />
                  {inscripcion.tutoria.hora_inicio.slice(0, 5)} -{" "}
                  {inscripcion.tutoria.hora_fin.slice(0, 5)}
                </span>
              </div>
              {inscripcion.tutoria.modalidad === "virtual" &&
                inscripcion.tutoria.link_virtual && (
                  <a
                    href={inscripcion.tutoria.link_virtual}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-sm text-primary hover:underline"
                  >
                    Enlace a la reunión virtual
                  </a>
                )}
              {inscripcion.tutoria.modalidad === "presencial" &&
                inscripcion.tutoria.ubicacion && (
                  <p className="text-sm text-muted-foreground">
                    <MapPin className="h-3 w-3 inline mr-1" />
                    {inscripcion.tutoria.ubicacion}
                  </p>
                )}
              {hasFeedback && (
                <div className="flex items-center gap-1 mt-2">
                  <span className="text-sm text-muted-foreground">
                    Tu calificación:
                  </span>
                  {Array.from({ length: 5 }).map((_, i) => (
                    <Star
                      key={i}
                      className={`h-4 w-4 ${
                        i < (inscripcion.feedback?.[0]?.calificacion || 0)
                          ? "fill-primary text-primary"
                          : "text-muted-foreground"
                      }`}
                    />
                  ))}
                </div>
              )}
              <div className="pt-1">
                <Button variant="outline" size="sm" onClick={() => toggleDetalle(inscripcion.id)}>
                  {detalleVisible ? "Ocultar detalles" : "Ver detalles"}
                </Button>
              </div>
              {detalleVisible && (
                <div className="mt-3 grid gap-3 rounded-xl bg-slate-50 p-4 text-sm text-slate-700 sm:grid-cols-2">
                  {inscripcion.tutoria.linkDrive ? (
                    <div className="sm:col-span-2">
                      <p className="font-semibold text-slate-900">Carpeta de Drive</p>
                      <a href={inscripcion.tutoria.linkDrive} target="_blank" rel="noreferrer" className="text-sky-700 underline underline-offset-2">
                        Abrir carpeta compartida
                      </a>
                    </div>
                  ) : null}
                  {inscripcion.tutoria.modalidad === "virtual" && inscripcion.tutoria.link_virtual ? (
                    <div className="sm:col-span-2">
                      <p className="font-semibold text-slate-900">Google Meet</p>
                      <a href={inscripcion.tutoria.link_virtual} target="_blank" rel="noreferrer" className="text-sky-700 underline underline-offset-2">
                        Ir a la reunión virtual
                      </a>
                    </div>
                  ) : null}
                  {inscripcion.tutoria.modalidad === "presencial" && inscripcion.tutoria.ubicacion ? (
                    <div className="sm:col-span-2">
                      <p className="font-semibold text-slate-900">Ubicación</p>
                      <p>{inscripcion.tutoria.ubicacion}</p>
                    </div>
                  ) : null}
                  {puedeGestionar ? (
                    <div className="sm:col-span-2">
                      <Button asChild variant="outline" size="sm">
                        <Link to="/dashboard/panel-tutor">Gestionar en Panel Tutor</Link>
                      </Button>
                    </div>
                  ) : null}
                </div>
              )}
            </div>
            {showActions && (
              <div className="flex gap-2 sm:flex-col">
                {!isPast && inscripcion.status === "inscripto" && (
                  <Button
                    variant="destructive"
                    size="sm"
                    onClick={() => handleCancelar(inscripcion.id)}
                    disabled={loading === inscripcion.id}
                  >
                    {loading === inscripcion.id ? (
                      <Loader2 className="h-4 w-4 animate-spin" />
                    ) : (
                      "Cancelar"
                    )}
                  </Button>
                )}
                {canLeaveFeedback && (
                  <Button size="sm" onClick={() => openFeedbackDialog(inscripcion.id)}>
                    <MessageSquare className="h-4 w-4 mr-1" />
                    Dejar feedback
                  </Button>
                )}
                <Button variant="outline" size="sm" asChild>
                  <Link to={`/dashboard/tutorias/${inscripcion.tutoria.id}`}>
                    Ver detalles
                  </Link>
                </Button>
              </div>
            )}
          </div>
        </CardContent>
      </Card>
    );
  };

  if (cargando) {
    return (
      <div className="flex justify-center py-12">
        <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
      </div>
    );
  }

  return (
    <div className="container mx-auto space-y-6 p-6">
      <div>
        <h1 className="text-2xl font-bold text-foreground">Mis Inscripciones</h1>
        <p className="text-muted-foreground">
          Gestiona tus inscripciones a tutorías
        </p>
      </div>

      <Tabs defaultValue="proximas" className="space-y-4">
        <TabsList>
          <TabsTrigger value="proximas" className="gap-2">
            <CalendarCheck className="h-4 w-4" />
            Próximas ({proximasInscripciones.length})
          </TabsTrigger>
          <TabsTrigger value="pasadas" className="gap-2">
            <Calendar className="h-4 w-4" />
            Pasadas ({pasadasInscripciones.length})
          </TabsTrigger>
          <TabsTrigger value="canceladas" className="gap-2">
            <CalendarX className="h-4 w-4" />
            Canceladas ({canceladasInscripciones.length})
          </TabsTrigger>
        </TabsList>

        <TabsContent value="proximas" className="space-y-4">
          {proximasInscripciones.length > 0 ? (
            proximasInscripciones.map((inscripcion) => (
              <InscripcionCard key={inscripcion.id} inscripcion={inscripcion} showActions />
            ))
          ) : (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <CalendarCheck className="h-16 w-16 text-muted-foreground/50 mb-4" />
                <h3 className="text-lg font-medium mb-2">
                  No tienes inscripciones próximas
                </h3>
                <p className="text-muted-foreground text-center mb-4">
                  Explora las tutorías disponibles y anótate
                </p>
                <Button asChild>
                  <Link to="/dashboard/tutorias">Ver tutorías disponibles</Link>
                </Button>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="pasadas" className="space-y-4">
          {pasadasInscripciones.length > 0 ? (
            pasadasInscripciones.map((inscripcion) => (
              <InscripcionCard key={inscripcion.id} inscripcion={inscripcion} showActions />
            ))
          ) : (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <Calendar className="h-16 w-16 text-muted-foreground/50 mb-4" />
                <h3 className="text-lg font-medium mb-2">
                  No tienes tutorías pasadas
                </h3>
                <p className="text-muted-foreground text-center">
                  Aquí aparecerán las tutorías a las que asististe
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="canceladas" className="space-y-4">
          {canceladasInscripciones.length > 0 ? (
            canceladasInscripciones.map((inscripcion) => (
              <InscripcionCard key={inscripcion.id} inscripcion={inscripcion} />
            ))
          ) : (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <CalendarX className="h-16 w-16 text-muted-foreground/50 mb-4" />
                <h3 className="text-lg font-medium mb-2">
                  No hay inscripciones canceladas
                </h3>
                <p className="text-muted-foreground text-center">
                  Las inscripciones que canceles aparecerán aquí
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>
      </Tabs>

      {/* Feedback Dialog */}
      <Dialog open={feedbackDialogOpen} onOpenChange={setFeedbackDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Dejar Feedback</DialogTitle>
            <DialogDescription>
              Tu opinión ayuda a mejorar las tutorías y a otros estudiantes a elegir
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label>Calificación</Label>
              <div className="flex gap-1">
                {Array.from({ length: 5 }).map((_, i) => (
                  <button
                    key={i}
                    type="button"
                    onClick={() =>
                      setFeedbackData({ ...feedbackData, calificacion: i + 1 })
                    }
                    className="p-1 hover:scale-110 transition-transform"
                  >
                    <Star
                      className={`h-8 w-8 ${
                        i < feedbackData.calificacion
                          ? "fill-primary text-primary"
                          : "text-muted-foreground"
                      }`}
                    />
                  </button>
                ))}
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="comentario">Comentario (opcional)</Label>
              <Textarea
                id="comentario"
                placeholder="¿Qué te pareció la tutoría?"
                value={feedbackData.comentario}
                onChange={(e) =>
                  setFeedbackData({ ...feedbackData, comentario: e.target.value })
                }
                rows={3}
              />
            </div>
            <div className="flex items-center space-x-2">
              <Checkbox
                id="anonimo"
                checked={feedbackData.es_anonimo}
                onCheckedChange={(checked) =>
                  setFeedbackData({ ...feedbackData, es_anonimo: Boolean(checked) })
                }
              />
              <Label htmlFor="anonimo" className="text-sm font-normal">
                Publicar como anónimo
              </Label>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setFeedbackDialogOpen(false)}>
              Cancelar
            </Button>
            <Button onClick={handleSubmitFeedback} disabled={loading === "feedback"}>
              {loading === "feedback" ? (
                <Loader2 className="h-4 w-4 animate-spin mr-2" />
              ) : null}
              Enviar feedback
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}