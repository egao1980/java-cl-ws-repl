(ql:quickload :websocket-driver-client)

(defvar *client* (wsd:make-client "ws://localhost:8080/repl"))

(wsd:start-connection *client*)

(wsd:on :message *client*
        (lambda (message)
          (format t "~&Got: ~A~%" message)))

(wsd:send *client* "var a = 19;")
(wsd:send *client* "a = a + 10;")
(wsd:send *client* "a")